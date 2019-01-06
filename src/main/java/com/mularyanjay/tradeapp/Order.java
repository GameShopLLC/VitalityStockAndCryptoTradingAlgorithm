package com.mularyanjay.tradeapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
// @JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String id;
    private String size;
    private String price;
    private String product_id;
    private String side;
    private String stp;
    private String type;
    private String time_in_force;
    private Boolean post_only;
    private String created_at;
    private String fill_fees;
    private String filled_size;
    private String executed_value;
    private String status;
    private Boolean settled;
    private String cancel_after;
    
    public Order() {}

	public Order(String id, String price, String size, String product_id, String side, String stp, String type, String time_in_force, Boolean post_only, String created_at, String fill_fees, String filled_size, String executed_value, String status, Boolean settled, String cancel_after) {
        this.id = id;
        this.price = price;
        this.size = size;
        this.product_id = product_id;
        this.side = side;
        this.type = type;
        this.time_in_force = time_in_force;
        this.post_only = post_only;
        this.created_at = created_at;
        this.fill_fees = fill_fees;
        this.filled_size = filled_size;
        this.executed_value = executed_value;
        this.status = status;
        this.settled = settled;
        this.cancel_after = cancel_after;
        this.stp = stp;
    }

    // public Order(String id, String price, String size, String product_id, String side, String stp, String type, String time_in_force, Boolean post_only, String created_at, String fill_fees, String filled_size, String executed_value, String status, Boolean settled) {
    //     this.id = id;
    //     this.price = price;
    //     this.size = size;
    //     this.product_id = product_id;
    //     this.side = side;
    //     this.stp = stp;
    //     this.type = type;
    //     this.time_in_force = time_in_force;
    //     this.post_only = post_only;
    //     this.created_at = created_at;
    //     this.fill_fees = fill_fees;
    //     this.filled_size = filled_size;
    //     this.executed_value = executed_value;
    //     this.status = status;
    //     this.settled = settled;
    // }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String getStp() {
		return stp;
	}

	public void setStp(String stp) {
		this.stp = stp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime_in_force() {
		return time_in_force;
	}

	public void setTime_in_force(String time_in_force) {
		this.time_in_force = time_in_force;
	}

	public Boolean getPost_only() {
		return post_only;
	}

	public void setPost_only(Boolean post_only) {
		this.post_only = post_only;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getFill_fees() {
		return fill_fees;
	}

	public void setFill_fees(String fill_fees) {
		this.fill_fees = fill_fees;
	}

	public String getFilled_size() {
		return filled_size;
	}

	public void setFilled_size(String filled_size) {
		this.filled_size = filled_size;
	}

	public String getExecuted_value() {
		return executed_value;
	}

	public void setExecuted_value(String executed_value) {
		this.executed_value = executed_value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getSettled() {
		return settled;
	}

	public void setSettled(Boolean settled) {
		this.settled = settled;
	}

	public String getCancel_after() {
		return cancel_after;
	}

	public void setCancel_after(String cancel_after) {
		this.cancel_after = cancel_after;
	}
    
    
}
