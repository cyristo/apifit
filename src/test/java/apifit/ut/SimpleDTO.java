package apifit.ut;

import apifit.contract.IJsonDTO;

public class SimpleDTO implements IJsonDTO {

	private String hotelCode = null;
	private Integer nights;
	private String productCode = null;
	private String dateIn = null;
	private EmbededDTO embededDTO = null;
	
	public int getJsonNodeLevel() {
		return 0;
	}
	
	public EmbededDTO getEmbededDTO() {
		return embededDTO;
	}
	public void setEmbededDTO(EmbededDTO embededDTO) {
		this.embededDTO = embededDTO;
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
