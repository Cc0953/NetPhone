package com.example.cc.netphone;


import org.litepal.crud.DataSupport;

public class DepartmentDataBases extends DataSupport {
    private String DepartmentID;
    private String department;
    private String departmentPic;

    public void setDepartmentPic(String departmentPic) {
        this.departmentPic = departmentPic;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDepartmentID(String departmentID) {
        this.DepartmentID = departmentID;
    }

    public String getDepartment() {
        return department;
    }
    public String getDepartmentID() {
        return DepartmentID;
    }


    public String getDepartmentPic() {
        return departmentPic;
    }


}
