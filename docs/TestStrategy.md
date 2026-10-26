# Test Strategy - Proton Mail Web

## 1. Scope
Application under test: Proton Mail Web (https://mail.proton.me), Chrome browser, free account.

Covered areas: Authentication, Compose and send, Drafts, Inbox actions, Folders and labels, Search, Filters, Attachments, Asynchronous flows (undo send), and one two-account flow.

Detailed test cases are in `docs/TestCases.csv` (46 cases, 27 automated test methods + 5 Excel-driven login runs).

## 2. Assumptions
- Two test accounts are available. Account 1 is the sender and also receives its own mails. Account 2 is used for the two-account test.
- Free plan is used, so paid features (custom schedule time, more than one filter, @pm.me sending) are not tested.
- Tests send mail to the own mailbox when a fresh message is needed. This keeps the tests independent and does not depend on old mails.
- Every test uses a unique subject (timestamp), so tests do not clash and can be re-run.

## 3. Risks
| Risk | Impact | How it is handled |
|------|--------|-------------------|
| Proton UI uses dynamic content, iframe for email body | Flaky locators | `data-testid` locators and explicit waits, iframe switch handled in `ComposeEmail` |
| Mail delivery is asynchronous | Test checks too early | Wait for the message row to appear instead of `Thread.sleep` |
| CAPTCHA / human verification after many logins | Login blocked | Few logins per test, manual fallback, see README troubleshooting |
| Test data pile up in mailbox | Slower mailbox over time | Unique subjects, filter test cleans its filter, mailbox can be cleaned manually |
| Free plan limits (1 filter) | Filter test fails on re-run | Filter is always deleted in `@AfterMethod` |

## 4. Priorities
- P0 - Critical: login, send, draft, two-account delivery
- P1 - High: inbox actions, labels, search, filters, attachments, undo send
- P2 - Medium: restore, read/unread, bulk, date search
- P3 - Low: boundary and edge cases

## 5. Test levels and approach
- UI end-to-end tests using Selenium + TestNG with Page Object Model.
- No API level tests, because Proton API is not public for mail operations and is encrypted.
- Test data is created through the UI itself (send mail to own mailbox).

## 6. Coverage rationale (why these 27 tests)
- Every area in the scope has at least the required number of automated tests (see table below).
- Each test checks a real state change, for example: mail is in Archive and gone from Inbox, draft values are same after reopen, mail is not in Sent after undo.
- Negative cases are covered for login, compose (no recipient, invalid recipient) and search (no result).
- Core user journey (login -> send -> receive) is covered by P0 tests, so a failure there blocks a release.

| Area | Required | Automated |
|------|----------|-----------|
| Authentication | 2 (incl. negative) | 4 |
| Compose / send | 3 (incl. negative) | 4 (+ two-account test) |
| Draft | 2 | 2 |
| Inbox / organization | 3 | 7 (star, archive, trash, label, read/unread, restore, bulk) |
| Search | 2 (incl. advanced) | 3 |
| Filters | 1 | 2 (match, non-match + disabled) |
| Async | 1 | 2 (undo send, scheduled send) |
| Attachments | 1 | 2 |

## 7. What is NOT automated and why
- CC/BCC *visibility to the receivers* (BCC hidden) - needs 3 mailboxes. Only entering CC/BCC and keeping them in a draft is automated.
- Scheduled send - custom time is a paid feature and real "tomorrow" scheduling cannot be waited for. Approach (implemented in `ScheduledSendTest`): only the *scheduled state* is automated with the free "Tomorrow" option (mail appears in Scheduled with the banner "will be sent", cancel moves it to Drafts and the Scheduled folder disappears). The actual delivery at 08:00 stays a manual check; a real delivery test would need a test environment with a fake clock or API. To test time-dependent logic without long tests, a test environment with a fake clock or API would be needed.
- Password-protected email - needs a receiver opening an external link; better as exploratory test.
- Date range search - date picker is brittle; low risk.
- 2FA, CAPTCHA, rate limit - unstable and can lock the test account.
- Attachment download and file size limit - browser download handling and big files are slow; keep manual.

## 8. Suite tiers (TestNG groups)
- `smoke` (5 tests, every pull request): loginWithValidCredentials, composeEmail, sendWithoutRecipient, draft, searchByKeyword.
- `regression` (all tests, nightly): includes smoke.
- Run with `mvn test -Dgroups=smoke` or `-Dgroups=regression`. CI: `.github/workflows/tests.yml`.
- Retry is deliberately not used: tests create their own unique data, so a failing test points to a real problem (locator, environment or defect) and should not be hidden by a re-run.

## 9. Exit criteria
- All P0 and P1 automated tests pass in two consecutive runs.
- Any failure has a screenshot in `reports/screenshots` and is either fixed or logged in `docs/DefectReport.md`.
