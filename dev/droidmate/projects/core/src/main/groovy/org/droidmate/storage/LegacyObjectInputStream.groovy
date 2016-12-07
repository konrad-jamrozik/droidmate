// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.storage

/**
 * <p>An {@link ObjectInputStream} that can account for changes in fully qualified names of the read/deserialized classes. 
 * If this stream reads a class whose fully qualified name is a key in the {@link LegacyObjectInputStream#classNameMapping}, 
 * then it will be instead read as class whose fully qualified name is given in the value of that key.
 *
 * </p><p>
 * Assumption here is that the name of the read class has changed since it has been serialized.
 *
 * </p><p>
 * The name changed from:<br/>
 * A fully qualified name (now obsolete), as given in the mapping key<br/>
 * -to-<br/>
 * a fully qualified name (current), as given in the value of that key.
 * </p>
 */
class LegacyObjectInputStream extends ObjectInputStream
{

  LegacyObjectInputStream(InputStream ins) throws IOException
  {
    super(ins)
  }

  public static Map<String, String> classNameMapping = initClassNameMapping()

  private static Map<String, String> initClassNameMapping()
  {
    Map<String, String> classNameMapping = [
      "org.droidmate.exceptions.DeviceExceptionMissing": "org.droidmate.exploration.actions.DeviceExceptionMissing",
    ]
    return Collections.unmodifiableMap(classNameMapping)
  }

  @Override
  protected ObjectStreamClass readClassDescriptor()
    throws IOException, ClassNotFoundException
  {
    ObjectStreamClass desc = super.readClassDescriptor()
    if (classNameMapping.containsKey(desc.name))
    {
      return ObjectStreamClass.lookup(Class.forName(classNameMapping[desc.name]))
    }
    return desc
  }
}
