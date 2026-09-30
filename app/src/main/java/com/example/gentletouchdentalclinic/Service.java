package com.example.gentletouchdentalclinic;

public class Service {

    private String id;
    private String serviceName;
    private String description;
    private String price;
    private String duration;
    private String imageName;

    public Service(){

    }

    public Service(
            String id,
            String serviceName,
            String description,
            String price,
            String duration,
            String imageName
    ){

        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.imageName = imageName;

    }


    public String getId(){
        return id;
    }

    public String getServiceName(){
        return serviceName;
    }

    public String getDescription(){
        return description;
    }

    public String getPrice(){
        return price;
    }

    public String getDuration(){
        return duration;
    }

    public String getImageName(){
        return imageName;
    }

    public void setId(String id){this.id = id;}

}