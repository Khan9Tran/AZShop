package com.azshop.models;

import java.io.Serializable;
import java.util.Date;

public class CartModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private int id;
    private int userId;
    private int storeId;
    private boolean isDeleted;
    private Date createAt;
    private Date updateAt;
    
	public CartModel() {
	}

	public CartModel(int id, int userId, int storeId, boolean isDeleted, Date createAt, Date updateAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.storeId = storeId;
		this.isDeleted = isDeleted;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	@Override
	public String toString() {
		return "CartModel [id=" + id + ", userId=" + userId + ", storeId=" + storeId + ", isDeleted=" + isDeleted
				+ ", createAt=" + createAt + ", updateAt=" + updateAt + "]";
	}
	
	
    
    
    
    
	
}
