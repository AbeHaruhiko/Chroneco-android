package jp.caliconography.chroneco.util.parse;

import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by abe on 2014/11/15.
 */
public class ParseObjectAsyncProcResult {
    private ParseObjectAsyncUtil.ProcType procType;
    private ParseObject procTarget;
    private ParseFile procTargetFile;

    public ParseObjectAsyncProcResult(ParseObjectAsyncUtil.ProcType procType, ParseObject procTarget) {
        setProcType(procType);
        setProcTarget(procTarget);
    }

    public ParseObjectAsyncProcResult(ParseObjectAsyncUtil.ProcType procType, ParseFile procTarget) {
        setProcType(procType);
        setProcTarget(procTargetFile);
    }

    public ParseObject getProcTarget() {
        return procTarget;
    }

    public void setProcTarget(ParseFile procTarget) {
        this.procTargetFile = procTarget;
    }

    public void setProcTarget(ParseObject procTarget) {
        this.procTarget = procTarget;
    }

    public ParseObjectAsyncUtil.ProcType getProcType() {
        return procType;
    }

    public void setProcType(ParseObjectAsyncUtil.ProcType procType) {
        this.procType = procType;
    }
}