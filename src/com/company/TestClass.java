package com.company;

import java.io.Serializable;

public class TestClass implements Serializable {
    private String name;
    private String email;
    private int age;
    private boolean isAwesome;
    static final long serialVersionUID = 1;

    public TestClass(String name, String email, int age, boolean isAwesome){
        this.name = name;
        this.email = email;
        this.age = age;
        this.isAwesome = isAwesome;
    }

    public void print(){
        System.out.println("Name: " + this.name);
        System.out.println("Email: " + this.email);
        System.out.println("Age: " + this.age);
        System.out.println("Awesome?: " + this.isAwesome);
    }
}
