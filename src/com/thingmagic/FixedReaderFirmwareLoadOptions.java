/*
 * Copyright (c) 2008 ThingMagic, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.thingmagic;

public class FixedReaderFirmwareLoadOptions extends FirmwareLoadOptions{

private boolean eraseContents;
    private boolean revertDefaultSettings;

    public FixedReaderFirmwareLoadOptions()
    {
        // default constructor
    }

    public FixedReaderFirmwareLoadOptions(boolean eraseFirmware)
    {
        this.eraseContents = eraseFirmware;
    }

    public FixedReaderFirmwareLoadOptions(boolean eraseFirmware, boolean revertSettings)
    {
        this.eraseContents = eraseFirmware;
        this.revertDefaultSettings = revertSettings;
    }

    public void setEraseFirmware(boolean eraseFirmware)
    {
        this.eraseContents = eraseFirmware;
    }

    public boolean getEraseFirmware()
    {
        return eraseContents;
    }

    public void setRevertDefaultSettings(boolean revertSettings)
    {
        this.revertDefaultSettings = revertSettings;
    }

    public boolean getRevertDefaultSettings()
    {
        return revertDefaultSettings;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("FixedReaderFirmwareLoadOptions:[");
        sb.append(String.format("%s ", "eraseContents: " + eraseContents));
        sb.append(String.format("%s ", "revertDefaultSettings: " + revertDefaultSettings));
        sb.append("]");

        return sb.toString();
    }
}
