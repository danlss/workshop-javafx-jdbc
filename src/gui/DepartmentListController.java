package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

//classe que recebe o evento emitido pela classe FormController (observer)

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;
	// referencias componentes de tela
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	@FXML
	TableColumn<Department, Department> tableColumnREMOVE;
	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = gui.util.Utils.currentStage(event); // referencia stage atual
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {

		// inicar comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// macete para acompanhar a altura da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	/*
	 * ACESSA O SERVICE CARREGA OS DEPARTAMENTOS ALOCA DEPARTAMENTOS NA OBSLIST
	 * OBSLIST É ASSOCIADA COM TABLEVIEW DEPARTAMENTOS SAO EXIBIDOS
	 */
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// instanciando e alocando a lista de departamentos para exibiçao
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);

		// acrescenta um botao com o texto 'edit' em cada linha da tabela
		initEditButtons(); // quando clicado abrirá uma janela de edição
		
		//acrescenta um botao com o texto 'remove' em cada linha da tabela
		initRemoveButtons(); //quando clicado abrirá uma janela de confirmação
	}

	// janelinha de dialogo
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		// parentStage = stage de quem criou a janelinha de dialogo
		try {
			// Instancia e carrega a view absoluteName
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// captura o controlador da tela que acabou de carregar acima
			DepartmentFormController controller = loader.getController();
			// injeta o departamento no controlador
			controller.setDepartment(obj);
			// injeçao dependencia departmentService
			controller.setDerpartmentService(new DepartmentService());

			// escuta o evento do método [this = este objeto desta classe]
			// se inscreve para receber o evento e quando o evento for disparado executa o
			// metodo da interface
			controller.subscribeDataChangeListener(this);

			// carregar os dados de obj no form
			controller.updateFormData();

			// quando se utiliza uma janela de dialogo na frente de um stage é necessario
			// instaciar um novo stage [palco na frente de outro]
			Stage dialogStage = new Stage();

			// configurar stage
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // nao pode ser redimensionada
			dialogStage.initOwner(parentStage); // pai da dialog
			dialogStage.initModality(Modality.WINDOW_MODAL); // enquanto nao fechar nao pode acessar a janela anterior
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	// atualizar os dados da tabela quando escutar que foram alterados
	public void onDataChanged() {
		updateTableView();

	}

	// função para implementação de edição dos itens
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", gui.util.Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	// remoção de departamento
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Tem certeza que quer deletar?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
			//remove
			service.remove(obj);
			//atualiza
			updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error ao remover", null, e.getMessage(), Alert.AlertType.ERROR);
			}
		}
	}

}
