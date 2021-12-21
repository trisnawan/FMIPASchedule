package com.mita.fmipaschedule.database;

import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.InterfaceModel;

import java.util.ArrayList;
import java.util.List;

public class DaysData {

    public interface DaysInterface{
        void response(InterfaceModel<DaysModel> response);
    }

    public interface DaysListInterface{
        void response(InterfaceModel<List<DaysModel>> response);
    }

    private List<DaysModel> days(){
        List<DaysModel> list = new ArrayList<>();
        list.add(new DaysModel(0, "Minggu"));
        list.add(new DaysModel(1, "Senin"));
        list.add(new DaysModel(2, "Selasa"));
        list.add(new DaysModel(3, "Rabu"));
        list.add(new DaysModel(4, "Kamis"));
        list.add(new DaysModel(5, "Jumat"));
        list.add(new DaysModel(6, "Sabtu"));
        return list;
    }

    public void gets(DaysListInterface daysInterface){
        daysInterface.response(new InterfaceModel<>(true, "", days()));
    }

}
