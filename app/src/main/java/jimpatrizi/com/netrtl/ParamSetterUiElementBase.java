package jimpatrizi.com.netrtl;

/**
 * Created by jamespatrizi on 9/30/17.
 */

public class ParamSetterUiElementBase<UiElement> {

    protected ParamSetterUiElementBase(final Parameters param, UiElement uiElement)
    {
        this.param = param;
        this.uiElement = uiElement;
    }

    final Parameters param;
    final UiElement uiElement;
}
