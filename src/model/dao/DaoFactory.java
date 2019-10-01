package model.dao;

import java.util.List;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;

public class DaoFactory {
	
	public static SellerDao createSellerDao() {
		try {
			return new SellerDaoJDBC(DB.getConnection());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static DepartmentDao createDepartmentDao() {
		try {
			return new DepartmentDaoJDBC(DB.getConnection());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
