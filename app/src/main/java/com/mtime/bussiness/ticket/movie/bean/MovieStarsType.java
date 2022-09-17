package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieStarsType extends MBaseBean {
    private String typeName;
    private String typeNameEn;
    private List<Person> persons;

    public String getTypeName() {
	return typeName;
    }

    public void setTypeName(final String typeName) {
	this.typeName = typeName;
    }

    public String getTypeNameEn() {
	return typeNameEn;
    }

    public void setTypeNameEn(final String typeNameEn) {
	this.typeNameEn = typeNameEn;
    }

    public List<Person> getPersons() {
	return persons;
    }

    public void setPersons(final List<Person> persons) {
	this.persons = persons;
    }
}
