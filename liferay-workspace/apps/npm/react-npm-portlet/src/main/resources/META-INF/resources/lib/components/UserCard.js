import React from 'react';
import {ClayCardWithUser} from '@clayui/card';

export default function UserCard({ firstName, lastName, imageId }) {
  return (
    <div className="col-md-4">
      <ClayCardWithUser
        // description={alternateName}
        href="#"
        name={firstName + ' ' + lastName}
        spritemap={Liferay.ThemeDisplay.getPathThemeImages()+'/clay/icons.svg'}
        userImageSrc={`/image/user_portrait?img_id=${imageId}`}
        // /image/user_male_portrait?img_id=298925&img_id_token=SZFxQQrM7TI0eCAeRld3EZeh3O0%3D&t=01e022f1-5e85-7cb0-88ca-8df419fac438
      />
  </div>
  );
}
