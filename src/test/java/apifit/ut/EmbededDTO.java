package apifit.ut;

import apifit.contract.IJsonDTO;

public class EmbededDTO implements IJsonDTO {

	private String hotelCode = null;
	private Integer nights = null;
	private String productCode = null;
	private String dateIn = null;
	
	public int getJsonNodeLevel() {
		return 1;
	}
	
	public String getHotelCode() {
		return hotelCode;
	}
	public void setHotelCode(String hotelCode) {
		this.hotelCode = hotelCode;
	}
	public Integer getNights() {
		return nights;
	}
	public void setNights(Integer nights) {
		this.nights = nights;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getDateIn() {
		return dateIn;
	}
	public void setDateIn(String dateIn) {
		this.dateIn = dateIn;
	}

}
