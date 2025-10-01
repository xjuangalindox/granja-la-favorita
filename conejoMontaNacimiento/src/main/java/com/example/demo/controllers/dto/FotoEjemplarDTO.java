package com.example.demo.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FotoEjemplarDTO {
    
    private Long id;

    private String publicId; // Unique identifier for the photo in the storage service
    private String secureUrl; // URL to access the photo securely

    @JsonIgnore
    private EjemplarDTO ejemplar;
}
