package com.socialapp.heyya.core.command;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

public class CompositeServiceCommand extends ServiceCommand{

	private List<ServiceCommand> commandList = new LinkedList<ServiceCommand>();
	
	public CompositeServiceCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }
	 public void addCommand(ServiceCommand command) {
	        commandList.add(command);
	    }

	    public void removeCommand(ServiceCommand command) {
	        commandList.remove(command);
	    }
	@Override
	protected Bundle perform(Bundle extras) throws Exception {
		Bundle params = extras;
        for (ServiceCommand command : commandList) {
            params = command.perform(params);
        }
        return params;
	}

}
