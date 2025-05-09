package akkre.ariessentials.client.gui.hud.formWheel;

import akkre.ariessentials.client.gui.hud.WheelSegment;
import akkre.ariessentials.client.gui.hud.formWheel.icon.FormIcon;
import akkre.ariessentials.config.ConfigDBCClient;
import akkre.ariessentials.constants.DBCForm;
import akkre.ariessentials.controllers.FormController;
import akkre.ariessentials.data.FormWheelData;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.data.form.FormStackable;
import akkre.ariessentials.network.PacketHandler;
import akkre.ariessentials.network.packets.form.DBCSaveFormWheel;
import akkre.ariessentials.network.packets.form.DBCSelectForm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

class FormWheelSegment extends WheelSegment {
    public HUDFormWheel parent;
    public Form form;
    public FormWheelData data = new FormWheelData();
    private FormIcon icon = null;

    FormWheelSegment(HUDFormWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    FormWheelSegment(HUDFormWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = data.slot = index;
    }

    public void selectForm() {
        PacketHandler.Instance.sendToServer(new DBCSelectForm(data.formID, data.isDBC).generatePacket());
    }

    public void setForm(int formID, boolean isDBC, boolean updateServer) {
        data.formID = formID;
        data.isDBC = isDBC;
        form = !data.isDBC ? (Form) FormController.getInstance().get(data.formID) : null;
        if (updateServer)
            PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
        icon = form != null ? new FormIcon(parent, form) : new FormIcon(parent, data.formID);
    }

    public void setForm(FormWheelData data, boolean updateServer) {
        this.data = data;
        form = !data.isDBC ? (Form) FormController.getInstance().get(data.formID) : null;
        if (updateServer)
            PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
        icon = form != null ? new FormIcon(parent, form) : new FormIcon(parent, data.formID);
    }

    public void removeForm() {
        data.reset();
        form = null;
        PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
        if (parent.hoveredSlot == index)
            parent.selectSlot(-1);

        parent.timeClosedSubGui = Minecraft.getSystemTime();
        icon = null;
    }


    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {
        if (data.formID != -1) {
            if (index == 1 || index == 5) {
                glTranslatef(0, 10, 0);
            } else if (index == 2 || index == 4) {
                glTranslatef(0, -10, 0);
            }
            if (ConfigDBCClient.AlteranteSelectionWheelTexture) {
                glScaled(0.7, 0.7, 1);
                switch(index){
                    case 0:
                        glTranslatef(0, -15f, 0);
                        break;
                    case 1:
                        glTranslatef(-12, -5, 0);
                        break;
                    case 2:
                        glTranslatef(-11, 3, 0);
                        break;
                    case 3:
                        glTranslatef(0, 12f, 0);
                        break;
                    case 4:
                        glTranslatef(10, 3, 0);
                        break;
                    default:
                        glTranslatef(13, -5, 0);
                }
            }
            if (data.isDBC) {
                if (!parent.dbcForms.containsKey(data.formID))
                    removeForm();
            } else {
                if (!parent.dbcInfo.hasFormUnlocked(data.formID))
                    removeForm();
            }
            if(icon != null){
                glTranslatef(0, (float) -icon.height /2, 0);
                icon.draw();
                glTranslatef(0, icon.height, 0);
            }
            drawCenteredString(fontRenderer, getFormName(), 0, 0, 0xFFFFFFFF);
        }
    }

    public String getFormName() {
        DBCData dbcData = DBCData.get(parent.dbcInfo.parent.player);

        if(!data.isDBC)
            return form != null ? getFormVariant(form, dbcData).menuName : "";

        return DBCForm.getMenuName(dbcData.Race, data.formID, dbcData.isForm(DBCForm.Divine));
    }

    private Form getFormVariant(Form form, DBCData dbcData) {
        boolean isLegendary = dbcData.isForm(DBCForm.Legendary);
        boolean isDivine = dbcData.isForm(DBCForm.Divine);
        boolean isMajin = dbcData.isForm(DBCForm.Majin);
        boolean isFused = dbcData.stats.isFused();

        FormStackable stackable = form.stackable;

        FormController formController = FormController.Instance;

        if(formController.has(stackable.fusionID) && isFused) {
            form = (Form) formController.get(stackable.fusionID);
            stackable = form.stackable;
        }

        if (formController.has(stackable.divineID) && isDivine) {
            form = (Form) formController.get(stackable.divineID);
        } else if (formController.has(stackable.legendaryID) && isLegendary) {
            form = (Form) formController.get(stackable.legendaryID);
        } else if (formController.has(stackable.majinID) && isMajin) {
            form = (Form) formController.get(stackable.majinID);
        }

        return form;
    }
}
