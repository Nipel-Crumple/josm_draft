package org.openstreetmap.josm.plugins.rasterfilters.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.josm.plugins.rasterfilters.model.FilterStateModel;
import org.openstreetmap.josm.plugins.rasterfilters.model.StateChangeListener;
import org.openstreetmap.josm.plugins.rasterfilters.values.SliderValue;

public class FilterGuiListener implements ChangeListener, FilterStateOwner, ItemListener{
	
	private StateChangeListener handler;
    private FilterStateModel filterState;
    
    public FilterGuiListener(StateChangeListener handler) {
    	this.handler = handler;
    }
    
    public void setFilterState(FilterStateModel state) {
    	this.filterState = state;
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		
		JSlider slider = (JSlider) e.getSource();
		
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
	public FilterStateModel getState() {
		return filterState;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
	}	
	
}