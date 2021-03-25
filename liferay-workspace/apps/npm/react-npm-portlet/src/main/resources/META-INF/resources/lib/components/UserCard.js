import React from 'react';
import {ClayCardWithUser} from '@clayui/card';

export default function UserCard({ firstName, lastName, middleName, imageId }) {
  return (
    <div className="col-md-4">
      <ClayCardWithUser
        href="#"
        name={firstName + ' ' + lastName + ' ' + middleName}
        spritemap={Liferay.ThemeDisplay.getPathThemeImages()+'/clay/icons.svg'}
        userImageSrc={`/image/user_portrait?img_id=${imageId}`}
      />
  </div>
  );
}
