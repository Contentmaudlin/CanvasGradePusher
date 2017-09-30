# Canvas Grade Pusher

## Spreadsheet Format
First thing you need to do is to ensure the format of the spreadsheet. You need to have rows with the headers that contain the strings `ID`, `Comments` and `Total` and `Late`. The rest are self-explanatory, `ID` corresponds to a unique integer Canvas assigns to students. Export the Grades CSV to get access to this info. 

Vaguely, the grades spreadsheet will look like this:

| Name        | ID           | UNI  | Late (TRUE/FALSE) | W1 (10) | ... | Comments | Total (100) 
| ------------- |:-------------:| -----:| ------:| ------:| ------:| --------:| ------ 
| Student 1  | 812739 | aa1111 | TRUE  | 2  | ... | bla bla | 75
| Student 2  | 783453 | aa2222 | FALSE | 5  | ... | bla bla | 87
| Student 3 |  289374   aa3333 | FALSE | 10 | ... | Cool! | 100 
| . | . | . | . | . |.| .| .
| . | . | . | . | . |.| .| .
| . | . | . | . | . |.| .| .

