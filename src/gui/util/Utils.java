package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	//acessa o stage aonde o controller que recebeu o event está [quando um botao é clicado o stage é capturado]
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	
	public static Integer tryParseToInt(String str) {
		try{
			return Integer.parseInt(str);
		}
		//caso nao venha um numero valido
		catch(NumberFormatException e) {
			return null;
		}
	}
}
