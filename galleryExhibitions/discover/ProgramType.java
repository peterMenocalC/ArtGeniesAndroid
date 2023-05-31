package com.art.genies.galleryExhibitions.discover;

import com.art.genies.apis.response.Program;

import java.util.List;

public class ProgramType {
    public String type;
    public List<Program> programList;

    public ProgramType(String type, List<Program> programList){
        this.type = type;
        this.programList = programList;
    }
}
