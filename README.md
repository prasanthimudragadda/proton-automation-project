# Proton Mail Web Automation

Selenium + TestNG + Maven automation suite for Proton Mail Web (https://mail.proton.me), written with Page Object Model.

## Prerequisites
- Java 17 or higher (JDK)
- Maven 3.8+
- Google Chrome (Selenium Manager downloads the matching driver automatically)
- Two Proton test accounts (account 2 is only needed for `TwoAccountTest`)

## Setup
1. Copy `src/test/resources/config.example.properties` to `src/test/resources/config.properties`.
2. Fill the values:

| Key | Meaning |
|-----|---------|
| proton.username / proton.password | Account 1 login (sender, also receives its own mails) |
| proton.email | Full email address of account 1 |
| proton.recipient | Email address of account 2 |
| proton.label | An existing label in account 1 (default `p`) |
| proton.recipient.username / proton.recipient.password | Account 2 login. Leave password empty to skip the two-account test |

Any value can also be given as an environment variable (`proton.password` -> `PROTON_PASSWORD`), which is useful for CI so the password is not stored in a file. `config.properties` is in `.gitignore`.

## Data driven testing (Excel)
`LoginDataDrivenTest` runs the same login test once for every row of `src/test/resources/testdata/LoginData.xlsx` (sheet `Login`) using a TestNG `@DataProvider` and `ExcelReader` (Apache POI).

| Username | Password | Expected | Description |
|----------|----------|----------|-------------|
| account username | password | `success` or `error` | free text, shown in the assertion message |

- `success` -> inbox must be displayed, `error` -> login error message must be displayed.
- To add a test case just add a row in Excel, no code change is needed.
- The sheet contains real passwords, so do not share the file publicly.
- Run only this test: `mvn test -Dtest=LoginDataDrivenTest`

## Run
```
mvn clean test                          # run all tests
mvn test -Dtest=LoginTest               # one class
mvn test -Dtest=SearchTest#searchByKeyword   # one test
mvn test -Dheadless=true                # without browser window
mvn test -Dgroups=smoke                 # 5 smoke tests (every PR)
mvn test -Dgroups=regression            # full suite (nightly)
mvn surefire-report:report-only site -DgenerateReports=false   # html report
```
- HTML report: `target/reports/surefire.html` (a copy of the last full run is in `reports/html/`)
- Raw results: `target/surefire-reports/`
- Screenshot of every failed test: `reports/screenshots/`, plus page URL and error text in `reports/logs/`

## Project structure
```
src/main/java
  Utilities/Waits.java        explicit waits and small helpers used by all pages
  basePages/                  page objects
    LoginPage, HomePage       login, inbox check, sign out
    ComposeEmail              composer, draft, attachment, undo
    InboxPage                 folders, message row actions, labels
    SearchPage                keyword and advanced search
    FilterPage                custom filters (account settings)
src/test/java
  base/BaseTest.java          browser start/stop, pages, config
  utils/ConfigReader.java     reads config.properties (env variable overrides)
  utils/TestListener.java     failure screenshot
  utils/ExcelReader.java      reads rows from an excel sheet for data driven tests
  tests/                      LoginTest, ComposeEmailTest, DraftEmailTest, InboxActionsTest,
                              SearchTest, FilterTest, AttachmentTest, UndoSendTest, ScheduledSendTest, TwoAccountTest,
                              LoginDataDrivenTest (excel)
testng.xml                    default suite (package `tests`), groups `smoke` / `regression`
.github/workflows/tests.yml   CI: smoke on pull request, regression nightly, report uploaded
src/test/resources
  config.properties           credentials and test data (not committed)
  testdata/sample.txt         attachment file
  testdata/LoginData.xlsx     login data for the data driven test (git-ignored, contains passwords; test has no rows if missing)
docs/                         strategy, test cases, test report, defect report
reports/                      failure screenshots
```

## Architecture note
- **Page Object Model**: each page class holds its locators and small actions, tests only call actions and assert. A UI change is fixed in one page class.
- **BaseTest** starts a new Chrome for every test, but normal tests share one Chrome profile (`target/chrome-profile`) so the account stays signed in. Reason: a login per test made Proton refuse logins after about 12 tests. `LoginPage.login` does nothing when the inbox is already shown. Login/logout/credential tests (`LoginTest`, `LoginDataDrivenTest`) override `useCleanSession()` and get a brand new, signed out browser. Tests stay independent because each one creates its own uniquely named data.
- **Waits** wraps `WebDriverWait` (20 sec, 60 sec for a new mail to arrive because Proton refreshes the list by polling) - no `Thread.sleep` anywhere. `clickWhenNotCovered` retries a click while a toast covers the button. Absence is checked with `invisibilityOfElementLocated`.
- **Locators** prefer `data-testid` (Proton keeps them stable) instead of CSS classes, which are generated.
- **Test data**: credentials in config, subjects are unique (`System.currentTimeMillis()`), and tests that need a mail send it to the own mailbox first.
- **Assertions check state**, for example mail is in Archive and not in Inbox, draft fields equal typed values, mail is not in Sent after Undo.
- **Failure evidence**: `TestListener` takes a screenshot on every failure. Surefire report gives the result summary.
- **Two accounts**: `TwoAccountTest` opens a second browser for the receiver.
- **Groups and CI**: every test is in `regression`; the five PR tests are also in `smoke`. GitHub Actions runs smoke on pull requests and the full suite nightly, with credentials from repository secrets (`PROTON_USERNAME`, `PROTON_PASSWORD`, `PROTON_EMAIL`, `PROTON_RECIPIENT`, `PROTON_RECIPIENT_USERNAME`, `PROTON_RECIPIENT_PASSWORD`, `PROTON_INCORRECTPWD`). `ConfigReader` works without `config.properties` when these variables are set.
- Not used on purpose: retry analyzer (would hide real defects and unique data makes re-runs unnecessary), parallel run (two shared mailboxes and a one-filter limit on the free plan), cross browser. Chrome only.

