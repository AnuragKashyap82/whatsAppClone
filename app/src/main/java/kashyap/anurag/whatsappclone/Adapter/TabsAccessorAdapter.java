package kashyap.anurag.whatsappclone.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kashyap.anurag.whatsappclone.ChatsFragment;
import kashyap.anurag.whatsappclone.ContactsFragment;
import kashyap.anurag.whatsappclone.GroupsFragment;
import kashyap.anurag.whatsappclone.RequestFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Group";
            case 2:
                return "Contact";
            case 3:
                return "Request";

            default:
                return null;
        }
    }
}
