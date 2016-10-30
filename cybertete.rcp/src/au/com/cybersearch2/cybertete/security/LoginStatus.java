/**
    Copyright (C) 2016  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.cybertete.security;

/**
 * LoginStatus
 * Final status of job on completion: no error, failed because of invalid password, fail for other reason.
 * The status allows error handling to be tailored for special cases.
 * The invalid password cass is important because the user can then be forced to retry entering the password.
 * @author Andrew Bowley
 * 14 Mar 2016
 */
public enum LoginStatus
{
    noError,
    invalidPassword,
    fail
}
