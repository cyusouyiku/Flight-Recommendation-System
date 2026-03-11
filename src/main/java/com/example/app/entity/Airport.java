package com.example.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String country;
    private String timezone;
}
