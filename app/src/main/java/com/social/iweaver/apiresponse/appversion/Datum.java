package com.social.iweaver.apiresponse.appversion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Datum {

@SerializedName("isUpdateRequired")
@Expose
public Boolean isUpdateRequired;
@SerializedName("isForceUpdate")
@Expose
public Boolean isForceUpdate;

public Datum withIsUpdateRequired(Boolean isUpdateRequired) {
this.isUpdateRequired = isUpdateRequired;
return this;
}

public Datum withIsForceUpdate(Boolean isForceUpdate) {
this.isForceUpdate = isForceUpdate;
return this;
}

}