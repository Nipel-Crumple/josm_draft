package gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map.Entry;

import javax.json.JsonObject;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import values.SliderValue;
import values.Value;

public class FilterGuiListener implements ChangeListener, FilterStateOwner, ItemListener {
	
//	private static final String name = "UnsharpMaskListener";
	private StateChangeListener handler;
    private FilterModel filterState;
    
    public FilterGuiListener(StateChangeListener handler) {
    	this.handler = handler;
    }
    
    public void setFilterState(FilterModel state) {
    	this.filterState = state;
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		
		JSlider slider = (JSlider) e.getSource();
		
//		System.out.println("filter state before " + filterState.toString());
		
		if (!slider.getValueIsAdjusting()) {
			slider.setToolTipText(String.valueOf((double) slider.getValue() / 100));
		}
		
		String parameterName = slider.getName();
		
		if (filterState.getParams().containsKey(parameterName)) {
			SliderValue<Number> value = (SliderValue<Number>) filterState.getParams().get(parameterName);
			if (value.isDouble()) {
				value.setValue((double) slider.getValue() / 100);
			} else {
				value.setValue(slider.getValue());
			}
			filterState.getParams().put(parameterName, value);
		}
		
		//notify about state is changed now so send msg to FiltersManager
		handler.filterStateChanged(filterState);
	}

	@Override
	public FilterModel getState() {
		//encoding new filter's state info to json for sending
		return filterState;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}
}