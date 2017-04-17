package com.example.cc.netphone;

import org.litepal.crud.DataSupport;


public class PhoneDataBases extends DataSupport{

    private String name;
    private String department;
    private String phone1;
    private String phone2;
    private String phone3;
    public PhoneDataBases(){};

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone3() {
        return phone3;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }
}

