package cn.ksmcbrigade.iui_nf.script.events;

import cn.ksmcbrigade.iui_nf.render.Element;
import cn.ksmcbrigade.iui_nf.render.constants.InputType;
import cn.ksmcbrigade.iui_nf.script.Event;

public class MouseEvent extends Event {

    public final int button;
    public final int delta;
    public final InputType input;
    public final Element target;

    public MouseEvent(int button, int delta, InputType input, Element target) {
        this.button = button;
        this.delta = delta;
        this.input = input;
        this.target = target;
    }
}
