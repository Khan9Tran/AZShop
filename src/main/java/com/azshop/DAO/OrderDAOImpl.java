package com.azshop.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.azshop.models.OrderModel;

public class OrderDAOImpl implements IOrderDAO {

	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	@Override
	public void insert(OrderModel order) {
		try {
			String sql = "INSERT INTO [Order] (userId, storeId, deliveryId, recipientName, address, phone, status, isPaidBefore, "
					+ "amountFromUser, amountFromStore, amountToStore, amountToAZShop, createAt) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
			
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1, order.getUserId());
			ps.setInt(2, order.getStoreId());
			ps.setInt(3, order.getDeliveryId());
			ps.setString(4, order.getRecipientName());
			ps.setString(5, order.getAddress());
			ps.setString(6, order.getPhone());
			ps.setString(7, order.getStatus());
			ps.setBoolean(8, order.isPaidBefore());
			ps.setBigDecimal(9, order.getAmountFromUser());
			ps.setBigDecimal(10, order.getAmountFromStore());
			ps.setBigDecimal(11, order.getAmountToStore());
			ps.setBigDecimal(12, order.getAmountToAZShop());
			
			ps.executeUpdate();
			
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public OrderModel getById(int id) {
		OrderModel order = new OrderModel();
		try {
			String sql = "Select *from [Order] where id = ?";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				order.setId(rs.getInt("id"));
				order.setUserId(rs.getInt("userId"));
				order.setStoreId(rs.getInt("storeId"));
				order.setDeliveryId(rs.getInt("deliveryId"));
				order.setRecipientName(rs.getString("recipientName"));
				order.setAddress(rs.getString("address"));
				order.setPhone(rs.getString("phone"));
				order.setStatus(rs.getString("status"));
				order.setPaidBefore(rs.getBoolean("isPaidBefore"));
				order.setAmountFromUser(rs.getBigDecimal("amountFromUser"));
				order.setAmountFromStore(rs.getBigDecimal("amountFromStore"));
				order.setAmountToStore(rs.getBigDecimal("amountToStore"));
				order.setAmountToAZShop(rs.getBigDecimal("amountToAZShop"));
				order.setCreateAt(rs.getDate("createAt"));
				order.setUpdateAt(rs.getDate("updateAt"));
				
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return order;
	}

	@Override
	public List<OrderModel> getAll() {
		List<OrderModel> oderModelList = new ArrayList<OrderModel>();
		
		try {
			String sql = "Select *from [Order]";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				OrderModel order = new OrderModel();
				order.setId(rs.getInt("id"));
				order.setUserId(rs.getInt("userId"));
				order.setStoreId(rs.getInt("storeId"));
				order.setDeliveryId(rs.getInt("deliveryId"));
				order.setRecipientName(rs.getString("recipientName"));
				order.setAddress(rs.getString("address"));
				order.setPhone(rs.getString("phone"));
				order.setStatus(rs.getString("status"));
				order.setPaidBefore(rs.getBoolean("isPaidBefore"));
				order.setAmountFromUser(rs.getBigDecimal("amountFromUser"));
				order.setAmountFromStore(rs.getBigDecimal("amountFromStore"));
				order.setAmountToStore(rs.getBigDecimal("amountToStore"));
				order.setAmountToAZShop(rs.getBigDecimal("amountToAZShop"));
				order.setCreateAt(rs.getDate("createAt"));
				order.setUpdateAt(rs.getDate("updateAt"));
				
				oderModelList.add(order);
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return oderModelList;
	}

	@Override
	public List<OrderModel> getByUserId(int userId) {
		List<OrderModel> oderModelList = new ArrayList<OrderModel>();
		
		try {
			String sql = "Select *from [Order] where userId = ?";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				OrderModel order = new OrderModel();
				order.setId(rs.getInt("id"));
				order.setUserId(rs.getInt("userId"));
				order.setStoreId(rs.getInt("storeId"));
				order.setDeliveryId(rs.getInt("deliveryId"));
				order.setRecipientName(rs.getString("recipientName"));
				order.setAddress(rs.getString("address"));
				order.setPhone(rs.getString("phone"));
				order.setStatus(rs.getString("status"));
				order.setPaidBefore(rs.getBoolean("isPaidBefore"));
				order.setAmountFromUser(rs.getBigDecimal("amountFromUser"));
				order.setAmountFromStore(rs.getBigDecimal("amountFromStore"));
				order.setAmountToStore(rs.getBigDecimal("amountToStore"));
				order.setAmountToAZShop(rs.getBigDecimal("amountToAZShop"));
				order.setCreateAt(rs.getDate("createAt"));
				order.setUpdateAt(rs.getDate("updateAt"));
				
				oderModelList.add(order);
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return oderModelList;
	}

	@Override
	public List<OrderModel> getByStoreId(int storeId) {
		List<OrderModel> oderModelList = new ArrayList<OrderModel>();
		
		try {
			String sql = "Select *from [Order] where storeId = ?";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, storeId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				OrderModel order = new OrderModel();
				order.setId(rs.getInt("id"));
				order.setUserId(rs.getInt("userId"));
				order.setStoreId(rs.getInt("storeId"));
				order.setDeliveryId(rs.getInt("deliveryId"));
				order.setRecipientName(rs.getString("recipientName"));
				order.setAddress(rs.getString("address"));
				order.setPhone(rs.getString("phone"));
				order.setStatus(rs.getString("status"));
				order.setPaidBefore(rs.getBoolean("isPaidBefore"));
				order.setAmountFromUser(rs.getBigDecimal("amountFromUser"));
				order.setAmountFromStore(rs.getBigDecimal("amountFromStore"));
				order.setAmountToStore(rs.getBigDecimal("amountToStore"));
				order.setAmountToAZShop(rs.getBigDecimal("amountToAZShop"));
				order.setCreateAt(rs.getDate("createAt"));
				order.setUpdateAt(rs.getDate("updateAt"));
				
				oderModelList.add(order);
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return oderModelList;
	}

	@Override
	public List<OrderModel> getByDeliveryId(int deliveryId) {
		List<OrderModel> oderModelList = new ArrayList<OrderModel>();
		
		try {
			String sql = "Select *from [Order] where deliveryId = ?";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, deliveryId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				OrderModel order = new OrderModel();
				order.setId(rs.getInt("id"));
				order.setUserId(rs.getInt("userId"));
				order.setStoreId(rs.getInt("storeId"));
				order.setDeliveryId(rs.getInt("deliveryId"));
				order.setRecipientName(rs.getString("recipientName"));
				order.setAddress(rs.getString("address"));
				order.setPhone(rs.getString("phone"));
				order.setStatus(rs.getString("status"));
				order.setPaidBefore(rs.getBoolean("isPaidBefore"));
				order.setAmountFromUser(rs.getBigDecimal("amountFromUser"));
				order.setAmountFromStore(rs.getBigDecimal("amountFromStore"));
				order.setAmountToStore(rs.getBigDecimal("amountToStore"));
				order.setAmountToAZShop(rs.getBigDecimal("amountToAZShop"));
				order.setCreateAt(rs.getDate("createAt"));
				order.setUpdateAt(rs.getDate("updateAt"));
				
				oderModelList.add(order);
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return oderModelList;
	}

	@Override
	public void update(OrderModel order) {
		try {
			String sql = "UPDATE [Order] SET userId = ?, storeId = ?, deliveryId = ?, recipientName = ?, address = ?, phone = ?, status = ?, isPaidBefore = ?, "
					+ "amountFromUser = ?, amountFromStore = ?, amountToStore = ?, amountToAZShop = ?, updateAt = GETDATE() where id=?";
			
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			
			
			ps.setInt(1, order.getUserId());
			ps.setInt(2, order.getStoreId());
			ps.setInt(3, order.getDeliveryId());
			ps.setString(4, order.getRecipientName());
			ps.setString(5, order.getAddress());
			ps.setString(6, order.getPhone());
			ps.setString(7, order.getStatus());
			ps.setBoolean(8, order.isPaidBefore());
			ps.setBigDecimal(9, order.getAmountFromUser());
			ps.setBigDecimal(10, order.getAmountFromStore());
			ps.setBigDecimal(11, order.getAmountToStore());
			ps.setBigDecimal(12, order.getAmountToAZShop());
			ps.setInt(13, order.getId());
			
			ps.executeUpdate();
			
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int id) {
		try {
			String sql = "DELETE [Order] WHERE id=?";
			conn = new DBConnection().getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			conn.close();
			} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
