# Canvas Grade Pusher

## Spreadsheet Format
First thing you need to do is to ensure the format of the spreadsheet. You need to have rows with the headers that contain the strings `ID`, `Comments` and `Total` and `Late`. The rest are self-explanatory, `ID` corresponds to a unique integer Canvas assigns to students. Export the Grades CSV to get access to this info. 

Vaguely, the grades spreadsheet will look like this:

| Name        | ID           | UNI  | Late (TRUE/FALSE) | W1 (10) | ... | Comments | Total (100) 
| ------------- |:-------------:| -----:| ------:| ------:| ------:| --------:| ------ 
| Student 1  | 812739 | aa1111 | TRUE  | 2  | ... | bla bla | 75
| Student 2  | 783453 | aa2222 | FALSE | 5  | ... | bla bla | 87
| Student 3 |  289374 |  aa3333 | FALSE | 10 | ... | Cool! | 100 
| . | . | . | . | . |.| .| .
| . | . | . | . | . |.| .| .
| . | . | . | . | . |.| .| .

## OAuth For the Google Sheets API

We need to download a `client_secret.json` file to do an OAuth handshake with the Google Sheets API.

1. Go here: <http://console.developers.google.com>. Log in with whichever account has access to the spreadsheet.
2. Under Library, find the Sheets API link. Enable the Sheets API by creating a project. Call it `CanvasGradePusher`. Make sure that when you go under the Sheets API, you see it as enabled.
3. Go under Credentials, click on Create Credentials, then OAuth Client ID. 
	- Application type: Other. Name it CanvasGradePusher.
	- Ignore the pop-up screen.

4. Find the credential you just created and download it. Rename the file as `client_secret.json` and store it in the directory where `GradePusher.java` lives.

## Filling in config.properties

### `SPREADSHET_ID`. 
All Google Sheets links will have the following format: `https://docs.google.com/spreadsheets/d/<SPREADSHEET_ID>/edit`. Grab that ID and set it as `SPREADSHEET_ID`

### `CANVAS_TOKEN`
Here we need to create an API token from Canvas to access the information. Click on "Account" on the top left, go on "Settings", scroll down and click on "New Access Token". Use whatever configurations you want, generate a token and set `CANVAS_TOKEN` to be that token.

### `COURSE_ID` and `ASSIGNMENT_ID`
Load the assignment for which you are pushing grades on your browser. The link will look like this: `https://CANVAS_LINK/courses/COURSE_ID/assignments/ASSIGNMENT_ID`. Fill those variables in as appropriate.

## Pushing Grades
1. Make sure that the Google Spreadsheet is published to the web. Go on File/Publish to the Web... to assure this. This won't expose the spreadsheet, just allow those with the link (and in our case, the grade pusher) to access it.
2. Compile the program by running `make`.
3. `make run` will do a test run. This will pop open a browser window. Log into your Google account to which your `client_secret.json` is associated to and give permission to read your program. The program will print the JSON it will send to Canvas to the screen, so look to see if everything is appropriate.
3. If everything is good to go, `make push` to push grades.
