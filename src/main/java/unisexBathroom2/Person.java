package unisexBathroom2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Data
@AllArgsConstructor
public class Person {
    private String name;
    
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    
    private String gender;
    private long bathroomTime;
    

}
