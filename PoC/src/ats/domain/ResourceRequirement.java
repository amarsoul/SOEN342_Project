package ats.domain;

// this is the resource requirement of the domain model, a service type needs a quantity of a resource
public class ResourceRequirement {
    
private final Resource resource;
private final int quantityNeeded;



public ResourceRequirement(Resource resource, int quantityNeeded) {
this.resource = resource;
this.quantityNeeded = quantityNeeded;
}



public Resource getResource() { 
return resource; 
}


public int getQuantityNeeded() { 
return quantityNeeded; 
}

}