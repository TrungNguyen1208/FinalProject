package ptit.nttrung.finalproject.ui.add_group;

import ptit.nttrung.finalproject.base.BaseView;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public interface AddGroupView extends BaseView {
    void addRoomForUser(final String roomId, final int userIndex);

    void hideDialogWait();
}
