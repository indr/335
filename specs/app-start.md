# Application Start and User Interface Selection

Since there are two user interfaces to navigate through and listen to content, the user has to make a choice upon application startup. This user interface selection has to be accessible for both the seeing and non-seeing user. In order to instruct a non-seeing user to do so, the Text-to-Speech component has to be initialized before presenting the options.

The app start procedure has to encompass this:

1. Text-to-Speech initialization with Locale.ENGLISH 
1. Text-to-Speech utterance "Welcome to 3-3-5" 
 
In case the initialization or the utterance fails, the app shows a message telling the user about the error and presents a "Continue" button which launches the list interface. 
 
If the utterance succeeded, the app launches the user interface selection activity. This activity contains of two buttons. Upon start of the activity, an instruction is spoken telling the user to make a choice.

## Button Interface Launch

Before the button interface is launched, the application checks for interfering accessibility services. Up to now, we know of to service types that conflict with the design of the button interface:

 - Touch exploration, and
 - Spoken feedback

If any enabled service is detected, the application launches an activity that tells the user about the interference and gives instructions to temporarily disable these services. This interface, like the start activity and the interface selection activity, has to be optimized for accessibility services.

At this point, the user can confirm the message/instruction and launch the button interface by clicking anywhere on the screen, or he goes back to the interface selection screen by pressing the back button on his device.
