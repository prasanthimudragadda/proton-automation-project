# Defect Report and Findings

Environment for all notes below: Chrome (latest), Windows 11, maximized window, Proton Mail Web 5.0.137.5, Proton Account 5.0.430.0, free plan, explored on 24 Oct 2026.

## Genuine defects
No functional defect was confirmed during exploration. Bugs are not created just to fill this section.

## Observations (not defects) - automation relevant risks and limitations

| # | Finding | Type | Impact on automation |
|---|---------|------|----------------------|
| 1 | The search box in the top bar (`search-keyword`) is **read-only**. Clicking it opens a search panel that contains a different input (`id=search-keyword`). `sendKeys` on the first one fails with "element not interactable". | Automation risk | `SearchPage` clicks the box first and then types in the panel input. |
| 2 | "Custom" schedule time shows a Plus logo (paid feature). Only "Tomorrow" and "Monday" are available on free plan. | Limitation | `ScheduledSendTest` uses "Tomorrow" and checks the scheduled state only; the real delivery stays manual, see strategy. |
| 3 | A "needs your permission to enable desktop notifications" banner and an "Upgrade" banner appear on top of the mailbox. | Automation risk | Could shift layout / cover elements. Locators use `data-testid` so they are not affected so far. |
| 4 | Archive, Spam and Trash links are hidden under "More" in the sidebar. The expanded/collapsed state is remembered by the account. | Automation risk | `InboxPage.openFolder` expands "More" only when the folder link is not present. |
| 5 | The attachment list in the composer is collapsed by default ("1 file attached - Show"). File names are visible only after clicking Show. | Automation risk | `ComposePage.isAttachmentDisplayed` expands the list first. |
| 6 | The email body is inside an iframe, and the toast "Message sent." disappears after a few seconds. | Automation risk | Frame switching helper in `BasePage`, explicit wait for the toast right after clicking Send. |
| 7 | Free plan allows only one custom filter. | Limitation | Filter test deletes its filter in `@AfterMethod`. |
| 8 | Full suite runs: 26 Oct 2026 31 of 32 (one test-code issue, fixed); 3 Nov 2026 27 of 32 (five failures caused by Proton anti-abuse protection after three runs in 90 minutes), see `docs/TestReport.md`. | Result | Locators were verified on the live builds 5.0.137.5 and 5.0.138.11. |
| 9 | The toolbar button for read state switches its test id: an unread message shows `toolbar:read` (mark as read), a read one shows `toolbar:unread`. The unread state of a row is the CSS class `unread`. | Automation risk | `InboxPage` has separate `markSelectedAsRead` / `markSelectedAsUnread` and checks the row class. |
| 10 | After scheduling, the "Scheduled" sidebar link appears a few seconds later (toast "Scheduling message") and disappears again when nothing is scheduled. Cancelling goes through the banner "Edit" button and an "Edit and reschedule" dialog; the mail is moved to Drafts. | Automation risk | `openScheduled` waits for the link; the test asserts that the link disappears after cancel. |
| 11 | `composer:address` is the container of the whole To/CC/BCC area, and the CC/BCC buttons exist twice while CC is closed (inside `composer:address` and inside `composer:to-field`). Recipient chips are `composer-addresses-item` inside `composer:to-field`, `composer:cc-field` or `composer:bcc-field`. | Automation risk | CC/BCC buttons are located inside `composer:to-field`; chips are read per field. |
| 12 | Negative compose tests (no recipient, invalid recipient) and earlier runs leave drafts behind in the Drafts folder (about 30 were present). | Test-data hygiene | Fixed on 3 Nov 2026: every test that creates a draft deletes it again, including the negative compose tests (best effort, so cleanup never changes their result). |
| 13 | Repeated logins are throttled: after about 12 logins within 6 minutes the sign-in form stayed filled with no error message and no inbox for several minutes (only account 1 was affected, account 2 could still log in). The page gives the user no feedback in this state. | Behaviour / possible usability issue | Normal tests share one signed-in browser profile; see README troubleshooting. Whether the missing message is intended should be checked manually. |
| 14 | The search "clear" (x) button empties the search box, but the result list and URL (`#keyword=...`) stay on the old no-result search until the user leaves the search, for example by opening Inbox. Not checked against Proton's documentation, so it is a finding to confirm, not a confirmed defect. | Observation / possible usability issue | `searchWithNoResult` asserts the empty search box and then opens Inbox. |
| 15 | The checkbox of a message row is an `<input>` with opacity 0 inside a label; Selenium treats it as not visible. A one-time "Make your inbox your own" popover can cover the message list. | Automation risk | Tests click the label and dismiss the popover. |
| 16 | In this build ticking a label in "Label as" applies it immediately and closes the dropdown; there is no "Apply" button any more. | Behaviour change | `InboxPage.applyLabel` waits for the dropdown to close. |
| 17 | A filter name that was used and deleted a moment before is rejected by the "Add filter" dialog (red field, Next disabled). | Behaviour / limitation | Each filter test uses a unique name. |
| 18 | Proton Mail Web uses a responsive layout: below about 1000 px width the top search box turns into an icon and the message toolbar (archive, trash, label as) is hidden. Seen on 3 Nov 2026, build 5.0.138.11, which also added inbox category tabs (Primary, Social, Promotions, Newsletters). | Automation risk | Tests must run with a desktop window size. See the headless correction below. |

## Corrections found on 3 Nov 2026 (headless run, Mac)
- `DriverFactory` / old `BaseTest`: in headless mode `window().maximize()` reset the window to 800x600 although `--window-size=1920,1080` was given. Proton then showed its compact layout and 9 Search and Inbox tests failed with "element not found" for `search-keyword` and the toolbar buttons, but only headless (the same tests passed with a visible browser). This would also have failed every nightly CI run, which is headless. Found from the failure screenshot (800 px wide, `reports/screenshots/searchWithNoResult_1793687308474.png`). Fix: maximize only when not headless. Re-run of the 10 Search and Inbox tests headless: 10 of 10 passed.
- `LoginDataDrivenTest`: TestNG writes data provider values into the test name, so the passwords from `LoginData.xlsx` appeared in the Surefire XML/HTML reports. Each row is now a `LoginCase` object whose name is only the description.

## Corrections found while checking the live app (26 Oct 2026)
These were bugs in the automation code, not in Proton:
- `FilterPage`: the confirm button of the "Delete filter?" dialog is `type="button"`, but the locator required `type="submit"`. The cleanup in `FilterTest` would have failed and left the filter in place, and the free plan allows only one filter, so every later run would have broken. Fixed.
- `ComposePage.openDraftEmail` (then called `ComposeEmail`): used `message-row:subject`, which does not exist in this build (the list uses `message-column:subject` and `message-item:<subject>`). `DraftEmailTest` would have timed out. Fixed to click the row by `message-item:<subject>`.
- `ComposeEmailTest`: the mail body contained the text "null" instead of the username. Fixed.

## Defect template

| Field | Details |
|-------|---------|
| Title | |
| Environment | Browser, OS, viewport, build/date |
| Severity / Priority | Reasoning, not only the label |
| Preconditions | Account / data / state |
| Steps to reproduce | 1. 2. 3. |
| Expected vs actual | |
| Evidence | Screenshot path, console log |
| Reproducibility | Always / often / intermittent (x of y runs) |
