# Test Execution Report

Results of the full regression run (`mvn clean test -Dgroups=regression`). Raw results: `reports/html/full-run-results.xml`, HTML report: `reports/html/surefire-report.html`, failure screenshots: `reports/screenshots/`.

| Item | Value |
|------|-------|
| Run date | 26 Oct 2026 (about 15:10 to 15:30, IST) |
| Browser | Chrome 156.0.8078.47 |
| OS | Windows 11 |
| Java / Maven | Java 25.0.2, Maven 3.10.0, Selenium 4.51.0, TestNG 7.12.0 |
| Application build | Proton Mail Web 5.0.137.5, Proton Account 5.0.430.0, free plan |
| Total tests | 32 (27 test methods + 5 data driven login runs) |
| Passed | 31 |
| Failed | 1 (`filterStarsMatchingMail`, fixed and re-run, see Failures) |
| Skipped | 0 |
| Total time | 1149 s (19 min 9 s), tests run one after another |

After the fix the two `FilterTest` tests were re-run alone and both passed (2 of 2, 178 s). The complete suite was **not** re-run in one go after that last fix.

## Results per test

| Test | Class | Result | Time (s) | Notes |
|------|-------|--------|----------|-------|
| loginWithInValidCred | LoginTest | Passed | 5.0 | negative |
| loginWithValidCredentials | LoginTest | Passed | 19.1 |  |
| sessionStaysAfterRefresh | LoginTest | Passed | 22.2 |  |
| signOut | LoginTest | Passed | 22.4 |  |
| ccAndBccArePreservedInDraft | ComposeEmailTest | Passed | 15.0 | nothing is sent |
| composeEmail | ComposeEmailTest | Passed | 46.8 |  |
| sendWithInvalidRecipient | ComposeEmailTest | Passed | 10.3 | negative |
| sendWithoutRecipient | ComposeEmailTest | Passed | 11.0 | negative |
| deleteDraft | DraftEmailTest | Passed | 13.8 |  |
| draft | DraftEmailTest | Passed | 10.8 |  |
| applyLabel | InboxActionsTest | Passed | 47.0 |  |
| archiveMessage | InboxActionsTest | Passed | 43.1 |  |
| bulkMoveToTrash | InboxActionsTest | Passed | 84.5 |  |
| deleteMessage | InboxActionsTest | Passed | 47.8 |  |
| markAsReadAndUnread | InboxActionsTest | Passed | 46.9 |  |
| restoreFromTrash | InboxActionsTest | Passed | 79.4 |  |
| starMessage | InboxActionsTest | Passed | 47.4 |  |
| advancedSearchWithKeywordAndSender | SearchTest | Passed | 45.7 |  |
| searchByKeyword | SearchTest | Passed | 46.3 |  |
| searchWithNoResult | SearchTest | Passed | 7.4 | negative |
| filterIgnoresNonMatchingAndDisabled | FilterTest | Passed | 101.0 |  |
| filterStarsMatchingMail | FilterTest | Failed | 29.0 | FAILED in the full run, passed after the fix (see Failures) |
| attachAndRemoveFile | AttachmentTest | Passed | 23.1 |  |
| sendEmailWithAttachment | AttachmentTest | Passed | 49.0 |  |
| undoSend | UndoSendTest | Passed | 20.2 | async: waits for the Undo toast, no sleeps |
| scheduleAndCancelSend | ScheduledSendTest | Passed | 26.9 | scheduled state only (Tomorrow), delivery stays manual |
| senderToReceiver | TwoAccountTest | Passed | 42.1 | two browsers, two accounts |
| loginWithExcelData (row 1) | LoginDataDrivenTest | Passed | 19.4 | Account 1 - valid username and password |
| loginWithExcelData (row 2) | LoginDataDrivenTest | Passed | 19.1 | Account 2 - valid username and password |
| loginWithExcelData (row 3) | LoginDataDrivenTest | Passed | 4.9 | Valid username with wrong password |
| loginWithExcelData (row 4) | LoginDataDrivenTest | Passed | 4.5 | Username that does not exist |
| loginWithExcelData (row 5) | LoginDataDrivenTest | Passed | 4.5 | Special characters as password |

## Failures

### FilterTest.filterStarsMatchingMail (failed in the full run, then fixed)
- **Error:** `waiting for element //button[@data-testid='filter-modal:next-button'] to be clickable, but the element was not enabled`
- **Screenshot:** `reports/screenshots/filterStarsMatchingMail_1793007964878.png` (the Filter Name field is outlined in red, "Next" is greyed out)
- **Cause:** test code, not an application defect. Both filter tests shared one filter name that was created once per class. The second test used the name that the first test had deleted a moment before, and Proton rejected it.
- **Action:** every filter test now creates its own unique name and keyword in a `@BeforeMethod`. Re-run of `FilterTest` alone: 2 of 2 passed.

## Earlier runs and what they showed
These are not part of the result above, they explain the fixes in the code.

| Run | Result | Cause of the failures |
|-----|--------|-----------------------|
| Smoke, 1st | 0 of 5 | TestNG skipped the setup methods when a group is selected, fixed with `alwaysRun = true` |
| Smoke, 2nd | 3 of 5 | composer still "Loading message" when typing, and a self-sent mail needs longer than 20 s to show in the list |
| Full, 1st | 11 of 32 | every test started a new browser and logged in; after about 12 logins in 6 minutes Proton stopped accepting logins for account 1. Tests now share one signed in browser profile |
| Full, 2nd | 24 of 32 | message checkbox has opacity 0 (click the label instead), onboarding popover covering the list, label applies at once without an Apply button, wrong label link locator, CC/BCC chips only exist while editing |
| Full, 3rd | 31 of 32 | the filter name clash described above |

## How to reproduce
```
mvn clean test -Dgroups=regression
mvn surefire-report:report-only site -DgenerateReports=false   # html report in target/reports/surefire.html
```
