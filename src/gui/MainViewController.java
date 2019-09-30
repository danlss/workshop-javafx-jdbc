package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	//Metodos tratar menu
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	//abre outra tela
	private synchronized void loadView(String absoluteName) {
		
		try {
			//carregar outra tela [/gui/About.fxml]
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			//carrega uma nova VBox
			VBox newVBox = loader.load();
			
			//referencia da cena para trabalhar com a janela principal
			Scene mainScene = Main.getMainScene();
			//referencia VBox da janela principal
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //pega o primeiro elemento da view (ScrollPane)
			//getContent já é uma referencia ao que existe dentro da scrollPane [VBox]
			
			
			/*   ACRESCENTA NOS FILHOS DO VBOX OS FILHOS DO VBOX DA JANELA ABOUT   
			 * PRESERVA O MENUBAR
			 * EXCLUI OS FILHOS DO VBOX
			 * INCLUIR O MENUBAR
			 * INCLUIR OS FILHOS DO VBOX DA JANELA ABOUT*/
			
			//referencia para o menu (primeiro filho do VBox da janela principal)
			Node mainMenu = mainVBox.getChildren().get(0);
			//limpar todos os filhos de mainVBox
			mainVBox.getChildren().clear();
			//add filhos de mainMenu
			mainVBox.getChildren().add(mainMenu);
			//add filhos de newVBox [Coleçao]
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			/* MANIPULA A CENA PRINCIPAL INCLUINDO NELA ALEM DO MENU PRINCIPAL, OS FILHOS DA JANELA QUE ESTIVER ABRINDO */
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
