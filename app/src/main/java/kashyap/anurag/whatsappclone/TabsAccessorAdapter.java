package kashyap.anurag.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FeedsFragment feedsFragment = new FeedsFragment();
                return feedsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 3:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 4:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Feeds";
            case 1:
                return "Chats";
            case 2:
                return "Group";
            case 3:
                return "Contact";
            case 4:
                return "Request";
            default:
                return null;
        }
    }
}
