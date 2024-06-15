package com.neo.test.anagrafica.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectedRecords  {

    private int totalRejected;
    private List<RejectedRecord> recordList;

}
