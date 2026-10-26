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

## Troubleshooting
- **Login page asks for CAPTCHA / human verification**: too many logins in a short time. Wait some minutes, or solve it once manually and re-run.
- **Test cannot find the mail after sending**: delivery is slow. The wait for a new mail is 60 sec (`InboxPage.isMessageDisplayed`), re-run or increase it.
- **Many tests fail at login with the form filled but no inbox**: too many logins in a short time, Proton refuses them for several minutes. Wait about 10 minutes. The shared browser profile in `target/chrome-profile` keeps the number of logins low; do not delete it between runs of single tests (`mvn clean` removes it).
- **A click is intercepted by a popover**: Proton shows one-time popovers ("Make your inbox your own"). `InboxPage.dismissOnboardingPopup` closes it; add similar handling if a new popup appears.
- **FilterTest fails with a leftover filter**: free account allows one filter. Delete it in Settings > Filters and re-run.
- **Locator fails after a Proton update**: open the page in Chrome DevTools, check the `data-testid` and fix it in the page class.
- **`FileNotFoundException config.properties`**: run Maven from the project root and create the config file (see Setup).

## Automation Quality Review

**1. Which tests did you deliberately choose NOT to automate, and why?**
CC/BCC visibility for the receivers (needs three mailboxes), the real delivery of a scheduled mail (the state and cancel are automated with "Tomorrow", but nobody waits until 08:00; a fake clock would be needed), password-protected mail (external receiver step), date range search (brittle date picker), CAPTCHA/rate limit/2FA (can lock the account, changes often) and attachment download or size limit (browser download and large files are slow). They have low regression value compared to their cost and are kept as manual/exploratory tests, listed in `docs/TestCases.csv`.

**2. Which 5 tests would you run on every pull request? Which nightly?**
Every PR (about 5-8 minutes, covers the main journey): `loginWithValidCredentials`, `composeEmail`, `draft`, `sendWithoutRecipient`, `searchByKeyword`.
Nightly: the full suite - inbox actions, filter, attachments, undo send, two-account test, and the negative login cases.
Trade-off: PR runs must be fast and stable so developers trust them. The nightly run can be slower and cover more, and a failure there is checked next morning.

**3. How would you diagnose a test that passes locally but fails intermittently in CI?**
First check the failure screenshot and logs from the CI run. Then compare environment: browser version, headless mode and window size (headless needs an explicit size), network speed and the shared test account. Run the single test many times in a loop in CI to measure the failure rate. Usually the cause is a timing problem (an element is not ready or a mail has not arrived), so I replace the wrong wait with a condition-based wait, or data clashes between parallel runs, so I make data unique. A retry is used only after the cause is understood, and never to hide a real defect.

**4. How would you reduce flakiness if the UI changes frequently?**
Keep locators only in page classes, prefer stable attributes (`data-testid`, roles, labels) over long XPath or CSS classes, and wait for a condition instead of time. Agree with developers to keep the `data-testid` attributes. Keep tests short and independent, and run a small smoke set on every build so a locator break is found early and fixed in one place.

**5. How would you scale from 20 to 200+ tests?**
Split the suite by tags (smoke, regression, nightly) and run it in parallel with one browser and one account per thread (a pool of test accounts). Move shared steps (login, send mail, create data) to reusable helpers or API/data setup so tests do not repeat slow UI steps. Add components for repeated parts (toolbar, composer, list), a test data factory, a CI pipeline with a report and screenshot upload, and a review rule that each new test has a clear priority and owner. Track the flaky tests and fix or remove them regularly.
