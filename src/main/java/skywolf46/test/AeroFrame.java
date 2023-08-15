package skywolf46.test;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import javax.swing.*;

public class AeroFrame extends JFrame {

    public AeroFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Testlabel");
        label.setOpaque(false);

        add(label);

        pack();

        enableAeroEffect();
    }

    private void enableAeroEffect() {
        NativeLibrary dwmapi = NativeLibrary.getInstance("dwmapi");
        WinDef.HWND aeroFrameHWND = new WinDef.HWND(Native.getWindowPointer(this));
        MARGINS margins = new MARGINS();
        margins.cxLeftWidth = -1;
        margins.cxRightWidth = -1;
        margins.cyBottomHeight = -1;
        margins.cyTopHeight = -1;
        //DwmExtendFrameIntoClientArea(HWND hWnd, MARGINS *pMarInset)
        //http://msdn.microsoft.com/en-us/library/aa969512%28v=VS.85%29.aspx
        Function extendFrameIntoClientArea = dwmapi.getFunction("DwmExtendFrameIntoClientArea");
        WinNT.HRESULT result = (WinNT.HRESULT) extendFrameIntoClientArea.invoke(WinNT.HRESULT.class,
                new Object[]{aeroFrameHWND, margins});
        if (result.intValue() != 0)
            System.err.println("Call to DwmExtendFrameIntoClientArea failed.");
    }

    /**
     * http://msdn.microsoft.com/en-us/library/bb773244%28v=VS.85%29.aspx
     */
    public class MARGINS extends Structure implements Structure.ByReference {
        public int cxLeftWidth;
        public int cxRightWidth;
        public int cyTopHeight;
        public int cyBottomHeight;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        new AeroFrame().setVisible(true);
    }

}