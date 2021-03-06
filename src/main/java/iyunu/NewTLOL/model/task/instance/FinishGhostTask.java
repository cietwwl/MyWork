package iyunu.NewTLOL.model.task.instance;

import iyunu.NewTLOL.message.TaskMessage;
import iyunu.NewTLOL.model.task.BaseTask;
import iyunu.NewTLOL.model.task.ETaskState;

/**
 * 完成一轮江湖追杀令
 * 
 * @author SunHonglei
 * 
 */
public class FinishGhostTask extends BaseTask {

	@Override
	public void finishTask() {
		if (state.equals(ETaskState.during)) {
			setState(ETaskState.finish);
			// ======刷新任务信息======
			TaskMessage.sendTask(role);

		}
	}

	@Override
	public BaseTask copy() {
		FinishGhostTask task = new FinishGhostTask();
		BaseTask.init(task, this);

		return task;
	}

//	@Override
//	public String encode() {
//		return this.id + "#" + this.type.ordinal() + "#" + this.state.ordinal() + "#" + this.timeOut + "#" + this.receiver;
//	}

}