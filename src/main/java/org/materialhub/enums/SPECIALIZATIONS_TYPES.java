package org.materialhub.enums;

public enum SPECIALIZATIONS_TYPES {

    // how to use -> var type = SPECIALIZATIONS_TYPES.valueOf("COMPREHENSIVE") ; specialization.type = type.type
    // why ? collage specializations = {WHERE specializations.type = branched} ,

    // general , credit             // AI, CS , CN
    COMPREHENSIVE("comprehensive") , BRANCHED("branched"), ;

    public final String type ;


    SPECIALIZATIONS_TYPES(String type){
        this.type= type;
    }

}
