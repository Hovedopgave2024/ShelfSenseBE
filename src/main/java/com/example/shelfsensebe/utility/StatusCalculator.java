package com.example.shelfsensebe.utility;

import org.springframework.stereotype.Component;

@Component
public class StatusCalculator
{
    public int calculateStatus(int stock, int safetyStock, int stockROP) {
        int median = (safetyStock + stockROP) / 2;
        if (stock > stockROP) {
            return 4;
        } else if (stock <= safetyStock) {
            return 1;
        } else if (stock <= median) { // stock > safetyStock is implied
            return 2;
        } else { // stock > median && stock <= stockROP
            return 3;
        }
    }
}
