# Test Execution Report

Results of the latest full regression run (`mvn test -Dgroups=regression -Dheadless=true`). Raw results: `reports/html/full-run-results.xml`, HTML report: `reports/html/surefire-report.html`, failure evidence: `reports/screenshots/` and `reports/logs/` (same file name = same failure).

| Item | Value |
|------|-------|
| Run date | 3 Nov 2026, 12:24 to 12:48 IST |
| Browser | Chrome 156.0.8078.80, headless, window 1920x1080 |
| OS | macOS 27.0.1 |
| Java / Maven | Java 25.0.5, Maven 3.9.8, Selenium 4.51.0, TestNG 7.12.0 |
| Application build | Proton Mail Web 5.0.138.11, free plan |
| Total tests | 32 (27 test methods + 5 data driven login runs) |
| Passed | 27 |
| Failed | 5 (environment: Proton anti-abuse protection, see Failures) |
| Skipped | 0 |
| Total time | 1418 s (23 min 38 s), tests run one after another |

**Summary:** no failure points to an application defect or to a test code problem. All 5 failed tests passed in the first run of the same day (11:52 IST) with the same test code. They failed in the third full run within 90 minutes (about 75 logins and 100 sent mails on the same accounts), when Proton started to show a CAPTCHA and to respond slowly. Re-running immediately was deliberately not done, because more logins risk locking the test account.

## Results per test

| Test | Class | Result | Time (s) | Notes |
|------|-------|--------|----------|-------|
| loginWithValidCredentials | LoginTest | Passed | 21.4 |  |
| loginWithInValidCred | LoginTest | Passed | 4.9 | negative |
| signOut | LoginTest | Passed | 30.7 |  |
| sessionStaysAfterRefresh | LoginTest | **Failed** | 29.6 | environment, see F1 |
| Account 1 - valid username and password | LoginDataDrivenTest | **Failed** | 26.4 | environment, see F1 |
| Account 2 - valid username and password | LoginDataDrivenTest | Passed | 14.7 |  |
| Valid username with wrong password | LoginDataDrivenTest | Passed | 6.9 | negative |
| Username that does not exist | LoginDataDrivenTest | **Failed** | 23.2 | environment (CAPTCHA), see F2 |
| Special characters as password | LoginDataDrivenTest | Passed | 19.9 | negative |
| composeEmail | ComposeEmailTest | Passed | 40.7 |  |
| sendWithoutRecipient | ComposeEmailTest | Passed | 10.2 | negative |
| sendWithInvalidRecipient | ComposeEmailTest | Passed | 13.8 | negative |
| ccAndBccArePreservedInDraft | ComposeEmailTest | Passed | 23.0 | nothing is sent, draft deleted |
| draft | DraftEmailTest | Passed | 9.9 | draft deleted afterwards |
| deleteDraft | DraftEmailTest | Passed | 15.9 |  |
| markAsReadAndUnread | InboxActionsTest | Passed | 39.7 |  |
| restoreFromTrash | InboxActionsTest | **Failed** | 25.3 | environment (slow send), see F3 |
| bulkMoveToTrash | InboxActionsTest | Passed | 91.2 |  |
| starMessage | InboxActionsTest | Passed | 56.3 |  |
| archiveMessage | InboxActionsTest | Passed | 49.1 |  |
| deleteMessage | InboxActionsTest | Passed | 42.6 |  |
| applyLabel | InboxActionsTest | Passed | 56.3 |  |
| searchByKeyword | SearchTest | Passed | 55.7 |  |
| searchWithNoResult | SearchTest | Passed | 7.4 | negative |
| advancedSearchWithKeywordAndSender | SearchTest | Passed | 49.5 | multi-condition |
| filterStarsMatchingMail | FilterTest | Passed | 45.1 | filter deleted afterwards |
| filterIgnoresNonMatchingAndDisabled | FilterTest | Passed | 87.8 | negative + state |
| attachAndRemoveFile | AttachmentTest | Passed | 8.9 |  |
| sendEmailWithAttachment | AttachmentTest | Passed | 59.9 |  |
| undoSend | UndoSendTest | Passed | 11.2 | async: waits for the Undo toast, no sleeps |
| scheduleAndCancelSend | ScheduledSendTest | Passed | 22.4 | scheduled state only (Tomorrow), delivery stays manual |
| senderToReceiver | TwoAccountTest | **Failed** | 69.7 | environment, see F1 |

## Failures

### F1 - Mailbox still "Loading Proton Mail..." 20 s after login (3 tests)
- **Tests:** `sessionStaysAfterRefresh`, `loginWithExcelData[Account 1 - valid username and password]`, `senderToReceiver` (receiver login).
- **Assertion:** `Inbox should be shown after login expected [true] but found [false]`.
- **Evidence:** `reports/screenshots/sessionStaysAfterRefresh_1793690114662.png`, `loginWithExcelData_1793689815331.png`, `senderToReceiver_1793690248271.png`: login was accepted, the screen shows the Proton loading logo.
- **Cause:** server side slowness after many logins in a short time. The same tests took 12-20 s in the first run of the day.
- **Classification:** environment / rate limit, not a defect. Not "fixed" by a longer timeout on purpose; see the risk note below.

### F2 - Human Verification (CAPTCHA) on login (1 test)
- **Test:** `loginWithExcelData[Username that does not exist]` expected the login error block.
- **Evidence:** `reports/screenshots/loginWithExcelData_1793689881049.png` shows the "Human Verification" puzzle instead of the error.
- **Cause:** Proton's anti-abuse protection after repeated logins. CAPTCHA is excluded from automation on purpose (TC07).

### F3 - "Sending message" did not finish within 20 s (1 test)
- **Test:** `restoreFromTrash`, in its setup step (`sendMailToSelf`): `IllegalStateException: 'Message sent.' toast did not appear`.
- **Evidence:** `reports/screenshots/restoreFromTrash_1793689548868.png` shows the "Sending message" spinner still running.
- **Cause:** slow sending after about 100 mails from the account on the same day. The setup step fails loudly with a clear message instead of letting the test fail later at an unrelated step.

**Risk and next step:** the suite shares two real accounts on the public service, so heavy use on one day hits Proton's protection. For a production suite: a pool of test accounts (one per parallel thread and spread over runs), a dedicated test environment without CAPTCHA, and API based test data setup to cut the number of UI logins and sends. A retry would hide this, so it is not used.

## Runs on 3 Nov 2026

| Run | Result | What it showed |
|-----|--------|----------------|
| 1. Full, headless, after refactoring (`pages/BasePage`, ThreadLocal driver, assertion messages) | 23 of 32 | 9 Search and Inbox tests failed only headless: `maximize()` reset the headless window to 800x600, so Proton showed its compact layout without search box and toolbar (evidence: `reports/screenshots/searchWithNoResult_1793687308474.png`). This bug was in the original code too and would have failed every nightly CI run. Fixed in `DriverFactory`. |
| 2. SearchTest + InboxActionsTest, headless, after the fix | 10 of 10 | fix confirmed |
| 3. Full, headless (this report) | 27 of 32 | 5 environment failures, see above. Also confirmed that the Excel passwords no longer appear in the reports (test names show only the description). |

## Earlier runs (26 Oct 2026, Windows 11, Chrome visible, build 5.0.137.5)

| Run | Result | Cause of the failures |
|-----|--------|-----------------------|
| Smoke, 1st | 0 of 5 | TestNG skipped the setup methods when a group is selected, fixed with `alwaysRun = true` |
| Smoke, 2nd | 3 of 5 | composer still "Loading message" when typing, and a self-sent mail needs longer than 20 s to show in the list |
| Full, 1st | 11 of 32 | every test started a new browser and logged in; after about 12 logins in 6 minutes Proton stopped accepting logins for account 1. Tests now share one signed in browser profile |
| Full, 2nd | 24 of 32 | message checkbox has opacity 0 (click the label instead), onboarding popover covering the list, label applies at once without an Apply button, wrong label link locator, CC/BCC chips only exist while editing |
| Full, 3rd | 31 of 32 | the two filter tests reused one filter name, Proton rejects a just-deleted name (`reports/screenshots/filterStarsMatchingMail_1793007964878.png`). Fixed with a unique name per test; FilterTest re-run 2 of 2 |

## How to reproduce
```
mvn test -Dgroups=regression -Dheadless=true
mvn surefire-report:report-only site -DgenerateReports=false   # html report in target/reports/surefire.html
```
