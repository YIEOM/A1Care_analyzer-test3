package isens.hba1c_analyzer.View;

public interface DisplayIView {

	void setImageId();
	void setButtonId();
	void setImage();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	void setBarGaugeImage(int drawable);
}
