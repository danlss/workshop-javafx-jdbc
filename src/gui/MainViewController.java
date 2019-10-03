package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

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
		loadView("/gui/SellerList.fxml",
				(SellerListController controller) ->{
					controller.setSellerService(new SellerService());
					controller.updateTableView();
				});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml",
				(DepartmentListController controller) ->{
					controller.setDepartmentService(new DepartmentService());
					controller.updateTableView();
				});
		
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	//abre outra tela
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		
		try {
			//carregar outra tela [/gui/About.fxml]
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			//carrega uma nova VBox
			VBox newVBox = loader.load();
			
			//referencia da cena para trabalhar com a janela principal
			Scene mainScene = Main.getMainScene();
			//referencia VBox da janela principal
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //pega o primeiro elemento da view (ScrollPane)
			//getContent j� � uma referencia ao que existe dentro da scrollPane [VBox]
			
			
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
			//add filhos de newVBox [Cole�ao]
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			/* MANIPULA A CENA PRINCIPAL INCLUINDO NELA ALEM DO MENU PRINCIPAL, OS FILHOS DA JANELA QUE ESTIVER ABRINDO */
			
			//ativar Consumer e retornar controller do tipo passado
			T controller =  loader.getController();
			initializingAction.accept(controller);
			//AS DUAS LINHAS ACIMA EXECUTAM A FUN��O QUE FOR PASSADA COMO ARGUMENTO
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
