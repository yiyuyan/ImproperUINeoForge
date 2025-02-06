package cn.ksmcbrigade.iui_nf.script.events;

import cn.ksmcbrigade.iui_nf.render.Element;
import cn.ksmcbrigade.iui_nf.render.constants.InputType;
import cn.ksmcbrigade.iui_nf.script.Event;

public class KeyEvent extends Event {

    public final int key, scan;
    public final InputType input;
    public final Element target;

    public KeyEvent(int key, int scan, InputType input, Element target) {
        this.key = key;
        this.scan = scan;
        this.input = input;
        this.target = target;
    }
}
