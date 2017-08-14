# ICT4NCD
(In progress)
Written by Grace Whitmore
For Ministry of Health, Vanuatu Government
1 May 2017

Run instructions:
Download and open the program in Android Studio to run

## General Information:
  - Only for Android devices (iOS and Windows require totally separate applications)
  - Local app only (meaning the data is saved only to your phone, you can’t access it from separate phones)
  - There is a login function, so multiple people can keep their data on the same phone, and protect their information
  - App is created for each individual to track his/her own health information, rather than for health professionals
  - Allows user to keep track of weight/height/BMI, blood sugar, and blood pressure
  - Users can look at graphs of their data to better assess their data over time
  - (Not completed) Users will be able to look at aggregate data in one big list (all information they submitted)
  - (Not completed) Users can export their data to an Excel file in order to give it to their doctor
  - Users can choose the language of the app (Bislama, English, or French)
  - Next to each item to submit (systolic blood pressure, blood sugar, etc) there is an info button that the user can click to get more information about what to enter and what the numbers mean. This window also contains an audio button so if users have a hard time reading they can listen to the information instead
  - Users can edit/delete each data item as needed

## Usage:
Create a new user, or log in to an existing user (local use only).
Once inside the app, click on one of the 5 options (All Data, Blood Pressure, Blood Sugar, Weight/BMI, or Graphs).
  Blood Pressure Section
    - Helps user track heart rate, systolic blood pressure, and diastolic blood pressure
    - Displays data with different colors to give user immediate feedback (blue for too low, green for good, yellow for slightly too high, orange for too high, red for way too high)
    Still needs to be implemented:
      - Add time so the user can add data multiple times a day if they want
      - Add a checkbox that they can tick/untick to show if they took their medicine that day
      - Get more information from MoH about Systolic/Diastolic blood pressure and heart rate so the user can learn more about them if they want
  Blood Sugar Section
    - Helps user track blood sugar
    - User must choose whether it’s a fasting blood sugar or not (this can be changed, more options can be added, etc. based on MoH standards)
    - Displays data with different colors to give user immediate feedback (blue for too low, green for good, red for too high)
    Still needs to be implemented:
      - Add time so the user can add data multiple times a day if they want
      - Potentially add more boxes to fill out (what they ate? Exercise? Would like advice from health professional on what influential factors should be tracked)
      - Get more information from MoH about blood sugar so the user can learn more about it if they want
  Weight/BMI section
    - Helps user track weight/BMI
    - User inputs their height when they setup their account (what other default/initial values should be added to login screen?), which is automatically added to the page, but can be edited if desired
    - User inputs their weight, and the app calculates their BMI
    - User can only input once each day (not necessary to do multiple times a day?)
    Still needs to be implemented:
      - Get more information from MoH about BMI so the user can learn more about it if they want

  Graph section
    - Shows a line graph of data over time
    - User chooses which data they would like to see
    - Colors in the background help the user see if their blood pressure/blood sugar etc is too high or low (same colors as in the lists)
    - User can click on a specific data point and get all the data from that date
    Still needs to be implemented:
      - Weight, BMI, Height, and Blood Sugar need to be added to the graph section
      - User controls (change which dates are shown, graph multiple things at once)

  All data section (not yet implemented)
    - Will aggregate all user data and organize it by date, making it easier for the user to look at everything at once



  
