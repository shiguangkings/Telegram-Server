/*
 *     This file is part of Telegram Server
 *     Copyright (C) 2015  Aykut Alparslan KOÇ
 *
 *     Telegram Server is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Telegram Server is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.telegram.tl.contacts;

import org.telegram.api.TLContext;
import org.telegram.api.TLMethod;
import org.telegram.api.UserStore;
import org.telegram.data.ContactModel;
import org.telegram.data.DatabaseConnection;
import org.telegram.data.UserModel;
import org.telegram.mtproto.ProtocolBuffer;
import org.telegram.tl.*;

public class GetContacts extends TLObject implements TLMethod {

    public static final int ID = 583445000;

    public String hash;

    public GetContacts() {
    }

    public GetContacts(String hash){
        this.hash = hash;
    }

    @Override
    public void deserialize(ProtocolBuffer buffer) {
        hash = buffer.readString();
    }

    @Override
    public ProtocolBuffer serialize() {
        ProtocolBuffer buffer = new ProtocolBuffer(32);
        serializeTo(buffer);
        return buffer;
    }

    @Override
    public void serializeTo(ProtocolBuffer buff) {
        buff.writeInt(getConstructor());
        buff.writeString(hash);
    }

    public int getConstructor() {
        return ID;
    }

    @Override
    public TLObject execute(TLContext context, long messageId, long reqMessageId) {
        ContactModel[] contactsAll = DatabaseConnection.getInstance().getContacts(context.getUserId());
        TLVector<TLContact> contacts = new TLVector<>();
        TLVector<TLUser> users = new TLVector<>();

        for (ContactModel u : contactsAll) {
            UserModel umc = UserStore.getInstance().getUser(u.phone);
            if (umc != null) {
                contacts.add(new Contact(umc.user_id, true));
                users.add(new UserContact(umc.user_id, umc.first_name, umc.last_name,
                        umc.username, umc.access_hash, umc.phone, new UserProfilePhotoEmpty(), umc.status));
            }

        }

        return new Contacts(contacts, users);
    }
}