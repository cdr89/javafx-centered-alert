# Java FX CenteredAlert implementation

## How to use in your code:

### If you need to invoke showAndWait() method, use showCenteredAndWait() instead:
```java
CenteredAlert alert = new CenteredAlert(AlertType.CONFIRMATION, getPrimaryStage());
alert.setTitle("Title");
alert.setHeaderText("Header Text");
alert.setContentText("Content text");

Optional<ButtonType> result = alert.showCenteredAndWait();
if (result.get() == ButtonType.OK) {
	// do something
} else {
	// do something else
}
```

### If you need to invoke show() method, use showCentered() instead:
```java
CenteredAlert alert = new CenteredAlert(AlertType.INFORMATION, getPrimaryStage());
alert.setTitle("Title");
alert.setHeaderText("Header Text");
alert.setContentText("Content text");

alert.showCentered();
```
