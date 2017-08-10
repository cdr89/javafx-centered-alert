import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.sun.javafx.tk.Toolkit;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class CenteredAlert extends Alert {

	protected Stage primaryStage;
	protected Object dialogFieldValue;

	public CenteredAlert(@NamedArg("alertType") AlertType alertType, Stage primaryStage) {
		super(alertType);
		init(primaryStage);
	}

	public CenteredAlert(@NamedArg("alertType") AlertType alertType, @NamedArg("contentText") String contentText,
			Stage primaryStage, ButtonType... buttons) {
		super(alertType, contentText, buttons);
		init(primaryStage);
	}

	protected void init(Stage primaryStage) {
		this.primaryStage = primaryStage;
		dialogFieldValue = getDialogFieldValue();
	}

	public Optional<ButtonType> showCenteredAndWait() {
		Toolkit.getToolkit().checkFxUserThread();

		if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
			throw new IllegalStateException("showAndWait is not allowed during animation or layout processing");
		}

		Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWING));
		if (getWidth() == Double.NaN && getHeight() == Double.NaN) {
			invokeMethodByName(dialogFieldValue, "sizeToScene");
		}

		invokeMethodByName(dialogFieldValue, "show");
		centerAlertInStage();
		hide();

		// this is slightly odd - we fire the SHOWN event before the show()
		// call, so that users get the event before the dialog blocks
		Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWN));

		invokeMethodByName(dialogFieldValue, "showAndWait");

		return Optional.ofNullable(getResult());
	}

	protected void centerAlertInStage() {
		double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
		double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;
		double width = getWidth();
		setX(centerXPosition - width / 2d);
		double height = getHeight();
		setY(centerYPosition - height / 2d);
	}

	protected void invokeMethodByName(Object dialogFieldValue, String methodName) {
		try {
			Method method = dialogFieldValue.getClass().getDeclaredMethod(methodName, new Class[] {});
			method.setAccessible(true);
			method.invoke(dialogFieldValue);
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
		} catch (SecurityException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
		}
	}

	protected Object getDialogFieldValue() {
		Field dialogFieldDefinition = null;
		Class<?> currentClass = this.getClass();
		while (dialogFieldDefinition == null || currentClass.getName() != Object.class.getName()) {
			System.out.println("Current class: " + currentClass.getName());
			try {
				dialogFieldDefinition = currentClass.getDeclaredField("dialog");
			} catch (NoSuchFieldException e) {
				// e.printStackTrace();
			} catch (SecurityException e) {
				// e.printStackTrace();
			}

			currentClass = currentClass.getSuperclass();
		}

		dialogFieldDefinition.setAccessible(true);
		Object dialogFieldValue = null;
		try {
			dialogFieldValue = dialogFieldDefinition.get(this);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		}
		return dialogFieldValue;
	}

}
