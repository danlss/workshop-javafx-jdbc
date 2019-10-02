package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	// dependencia para o departamento
	private Department entity;

	private DepartmentService service;
	
	//lista de objetos que implementam a interface
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	// controlador com instancia de departamento
	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDerpartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	// botao salvar departamento no BD
	public void onBtSaveAction(ActionEvent event) {
		// programaçao defensiva pois a injeção de dependencia está sendo feita
		// manualmente
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			
			notifyDataChangeListeners();
			
			//fechar janelinha após salvar
			Utils.currentStage(event).close(); //pega referencia da janela e fecha
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
		}

	}

	//executa o método onDataChanged da interface em cada um dos listeners
	private void notifyDataChangeListeners() {
			dataChangeListeners.forEach(listener -> listener.onDataChanged());
	}

	// pega os dados do form e retorna novo obj
	private Department getFormData() {
		Department obj = new Department();

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());

		return obj;
	}

	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); //pega referencia da janela e fecha
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	// exibe nas caixas de texto os atributos do departamento
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
